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
                            @clear="getPromosList"
                    >
                        <el-button
                                slot="append"
                                icon="el-icon-search"
                                @click="handleCurrentChange(1);getPromosList;"
                        ></el-button>
                    </el-input>
                </el-col>
            </el-row>

            <el-table :data="promoslist" stripe border style="width: 100%">
                <el-table-column type="index"> </el-table-column>
                <el-table-column prop="promoName" label="活动名称"></el-table-column>
                <el-table-column
                        prop="promoItemPrice"
                        label="活动价格（元）"
                        width="95px"
                ></el-table-column>
                <el-table-column
                        prop="itemId"
                        label="商品ID"
                        width="70px"
                ></el-table-column>
                <el-table-column prop="startDate" label="开始时间"></el-table-column>
                <el-table-column prop="endDate" label="结束时间"></el-table-column>

                <el-table-column label="操作" width="130px">
                    <template v-slot="scope">
                        <el-button
                                size="mini"
                                type="primary"
                                icon="el-icon-thumb"
                                @click="publishPromo(scope.row.id)"
                        ></el-button>

                        <el-button
                                size="mini"
                                type="warning"
                                icon="el-icon-delete"
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


    </div>
</template>

<script>
    export default {
        data() {
            return {
                promoslist: [],
                editDialogVisible: false,
                editItemForm: {},

                queryInfo: {
                    query: "",
                    pagenum: 1,
                    pagesize: 10
                },
                query: "",
                total: 0,

            };
        },
        methods: {
            async getPromosList() {
                let params = new URLSearchParams();
                params.append("name", this.query)
                params.append("token", window.sessionStorage.getItem("token"))
                const { data:res } = await this.$http.post("/promo/selectByName", params);
                console.log(res)
                if (res.status !== "success") {
                    return this.$message.error(res.data);
                }
                var list = res.data
                for(var i = 0; i < list.length; i ++){
                    list[i].startDate = new Date(list[i].startDate.millis).toDateString();
                    list[i].endDate = new Date(list[i].endDate.millis).toDateString();
                }
                this.promoslist = list;
                this.total = this.promoslist.length;


            },
            handleSizeChange(newSize) {
                this.queryInfo.pagesize = newSize;
                this.getPromosList();
            },
            handleCurrentChange(newPage) {
                this.queryInfo.pagenum = newPage;
                this.getPromosList();
            },
            publishPromo(id) {
                let params = new URLSearchParams();
                params.append("id", id)
                params.append("token", window.sessionStorage.getItem("token"))
                const { data:res } = this.$http.post("/promo/publish", params)
                return this.$message.success(res.data);
            },

        },
        created() {
            this.getPromosList();
        },

    };
</script>

<style lang="less" scoped></style>
