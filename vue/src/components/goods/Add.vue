<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item>添加商品</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-alert
        title="添加商品信息"
        :closable="false"
        center
        show-icon
        type="info"
      >
      </el-alert>
      <el-steps
        :space="500"
        :active="activeIndex - 0"
        finish-status="success"
        align-center
      >
        <el-step title="基本信息"></el-step>
        <el-step title="商品图片"></el-step>
        <el-step title="完成"></el-step>
      </el-steps>

      <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addFormRules"
        label-width="80px"
        label-position="top"
      >
        <el-tabs
          v-model="activeIndex"
          :tab-position="'left'"
        >
          <el-tab-pane label="基本信息" name="0">
            <el-form-item label="商品名称" prop="title">
              <el-input v-model="addForm.title"></el-input>
            </el-form-item>
            <el-form-item label="商品价格" prop="price">
              <el-input v-model="addForm.price" type="number"></el-input>
            </el-form-item>
            <el-form-item label="商品銷量" prop="sales">
              <el-input v-model="addForm.sales" type="number"></el-input>
            </el-form-item>
            <el-form-item label="商品庫存" prop="stock">
              <el-input v-model="addForm.stock" type="number"></el-input>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="商品图片" name="3">
            <el-upload
                    action="https://www.liulongbin.top:8888/api/private/v1/upload"
                    :headers="headerobj"
                    :on-preview="handlePreview"
                    :on-remove="handleRemove"
                    :on-success="handleSuccess"
                    list-type="picture"
            >
              <el-button size="small" type="primary">点击上传</el-button>
            </el-upload>
          </el-tab-pane>


          <el-tab-pane label="商品内容" name="4">
            <quill-editor v-model="addForm.description"></quill-editor>
            <el-button type="primary" class="btnAdd" @click="add">添加商品</el-button>
          </el-tab-pane>
        </el-tabs>
      </el-form>
    </el-card>

    <el-dialog
      title="图片预览"
      :visible.sync="previewVisible"
      width="50%">
      <img :src="previewPath" alt="" class="previewImg">
    </el-dialog>
  </div>
</template>

<script>

export default {
  data() {
    return {
      activeIndex: "0",
      addForm: {
        title: "",
        price: 0,
        sales: 0,
        stock: 0,
        pics: [],
        description:'',
      },
      addFormRules: {
        title: [
          { required: true, message: "请输入商品名称", trigger: "blur" }
        ],
        price: [
          { required: true, message: "请输入商品价格", trigger: "blur" }
        ],
        goods_wight: [
          { required: true, message: "请输入商品重量", trigger: "blur" }
        ],
        stock: [
          { required: true, message: "请输入商品数量", trigger: "blur" }
        ],
        sales: [
          { required: true, message: "请输入商品銷量", trigger: "blur" }
        ],
      },
      manyTabelData: [],
      onlyTabelData: [],
      headerobj:{
        Authorization:"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjUwMCwicmlkIjowLCJpYXQiOjE2MDg4OTc3NzEsImV4cCI6MTYwODk4NDE3MX0.kNugRvx9pHEKPPfwXxRliMLNeKEph5eWJyl89q2rrQk"
      },
      previewPath:'',
      previewVisible:false
    };
  },
  methods: {
    handlePreview(file) {
      this.previewPath = file.response.data.url
      this.previewVisible = true
    },
    handleRemove(file) {
      const picPath = file.response.data.tmp_path
      const i = this.addForm.pics.findIndex(x=>x.pic===picPath)
      this.addForm.pics.splice(i,1)
    },
    handleSuccess(response) {
      const picInfo = {pic:response.data.tmp_path}
      this.addForm.pics.push(picInfo)
    },
    add() {
      this.$refs.addFormRef.validate(async valid=>{
        if(!valid) {
          return this.$message.error('请先填写必填项！')
        }
        console.log(this.addForm.pics[0])
        var img = "https://www.liulongbin.top:8888/"+this.addForm.pics[0].pic
        console.log(img)
        let params = new URLSearchParams();
        params.append('title', this.addForm.title)
        params.append('price', this.addForm.price)
        params.append('stock', this.addForm.stock)
        params.append('sales', this.addForm.sales)
        params.append('description', this.addForm.description)
        params.append('imgUrl', img)
        params.append("token", window.sessionStorage.getItem("token"))
        console.log(params)
        const { data:res } = await this.$http.post("/item/add/", params);
        if (res.status !== 'success') {
          return this.$message.error(res.data);
        }
        this.$message.success('添加商品成功！')
        this.$router.push('/goods')
      })
    }
  },
};
</script>

<style lang="less" scoped>
.el-checkbox {
  margin: 0 10px 0 0 !important;
}

.previewImg{
  width: 100%;
}

.btnAdd{
  margin-top: 15px;
}
</style>
