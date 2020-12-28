<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item>商品列表</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            placeholder="请输入内容"
            clearable
            v-model="query"
            @clear="getGoodsList"
          >
            <el-button
              slot="append"
              icon="el-icon-search"
              @click="handleCurrentChange(1);getGoodsList;"
            ></el-button>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="goAddPage">添加商品</el-button>
        </el-col>
      </el-row>

      <el-table :data="goodslist" stripe border style="width: 100%">
        <el-table-column type="index"> </el-table-column>
        <el-table-column prop="title" label="商品名称"></el-table-column>
        <el-table-column
          prop="price"
          label="商品价格（元）"
          width="95px"
        ></el-table-column>
        <el-table-column
          prop="stock"
          label="商品庫存"
          width="70px"
        ></el-table-column>
        <el-table-column
                prop="sales"
                label="商品銷量"
                width="70px"
        ></el-table-column>
        <el-table-column prop="description" label="商品描述"></el-table-column>
        <el-table-column label="操作" width="130px">
          <template v-slot="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-edit"
              @click="showEditDialog(scope.row.id)"
            ></el-button>

            <el-button
              size="mini"
              type="warning"
              icon="el-icon-delete"
              @click="removeById(scope.row.id)"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>





      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryInfo.pagenum"
        :page-sizes="[10, 20, 50]"
        :page-size="queryInfo.pagesize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        background
      >
      </el-pagination>
    </el-card>

    <!-- 修改用户对话框 -->
    <el-dialog
            title="修改用户"
            :visible.sync="editDialogVisible"
            width="50%"
            @close="editDialogClosed"
    >
      <el-form
              :model="editItemForm"
              ref="editItemFormRef"
              label-width="70px"
              class="demo-ruleForm"
      >
        <el-form-item label="商品名稱">
          <el-input v-model="editItemForm.title" disabled></el-input>
        </el-form-item>
        <el-form-item label="商品價格" prop="price">
          <el-input v-model="editItemForm.price"></el-input>
        </el-form-item>
        <el-form-item label="商品庫存" prop="stock">
          <el-input v-model="editItemForm.stock"></el-input>
        </el-form-item>
        <el-form-item label="商品銷量" prop="sales">
          <el-input v-model="editItemForm.sales"></el-input>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="editItemForm.description"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="editItem">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      editDialogVisible: false,
      editItemForm: {},

      queryInfo: {
        query: "",
        pagenum: 1,
        pagesize: 10
      },
      query: "",
      goodslist: [],
      total: 0
    };
  },
  methods: {
    async getGoodsList() {
      let params = new URLSearchParams();
      params.append("name", this.query)
      params.append("token", window.sessionStorage.getItem("token"))
      const { data:res } = await this.$http.post("/item/select", params);
      console.log(res)
      if (res.status !== "success") {
        return this.$message.error(res.data);
      }
      this.goodslist = res.data;
      this.total = this.goodslist.length;
    },
    handleSizeChange(newSize) {
      this.queryInfo.pagesize = newSize;
      this.getGoodsList();
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pagenum = newPage;
      this.getGoodsList();
    },
    editItem() {
      this.$refs.editItemFormRef.validate(async valid => {
        if (!valid) return;
        let params = new URLSearchParams();
        console.log(this.editItemForm)
        params.append("id", this.editItemForm.id)
        params.append("price", this.editItemForm.price)
        params.append("stock", this.editItemForm.stock)
        params.append("sales", this.editItemForm.sales)
        params.append("description", this.editItemForm.description)
        params.append("token", window.sessionStorage.getItem("token"))
        const { data:res } = await this.$http.post("/item/update/", params);
        if (res.status !== "success") {
          this.$message.error("修改商品信息失败！");
        }
        this.$message.success("修改成功！");
        this.editDialogVisible = false;
        this.getGoodsList();
      });
    },
    async showEditDialog(id) {
      let params = new URLSearchParams();
      params.append("id", id)
      params.append("token", window.sessionStorage.getItem("token"))
      const { data:res } = await this.$http.post("/item/get/", params);
      if (res.status !== "success") {
        this.$message.error("查詢商品失败！");
      }
      this.editItemForm = res.data;
      console.log(this.editItemForm)
      this.editDialogVisible = true;
    },
    editDialogClosed() {
      this.$refs.editItemFormRef.resetFields();
    },
    removeById(id) {
      this.$confirm("此操作将永久删除该商品, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(async () => {
          console.log(id)
          let params = new URLSearchParams();
          params.append("id", id)
          params.append("token", window.sessionStorage.getItem("token"))
          const { data:res } = await this.$http.post("/item/delete", params);
          if (res.status !== "success") {
            return this.$message.error(res.data);
          }
          this.getGoodsList();
          this.$message({
            type: "success",
            message: "删除成功!"
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    goAddPage() {
      this.$router.push("goods/add");
    }
  },
  created() {
    this.getGoodsList();
  },

};
</script>

<style lang="less" scoped></style>
