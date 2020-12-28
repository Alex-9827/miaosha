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
            </el-row>

            <el-table :data="goodslist" stripe border style="width: 100%">
                <el-table-column type="index"> </el-table-column>
                <el-table-column prop="title" label="商品名称"></el-table-column>
                <el-table-column
                        prop="price"
                        label="商品价格（元）"
                        width="130px"
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
                <el-table-column label="查看详情" width="70px">
                    <template v-slot="scope">
                        <el-button
                                size="mini"
                                type="primary"
                                icon="el-icon-thumb"
                                @click="goDetailPage(scope.row.id)"
                        ></el-button>
                    </template>
                </el-table-column>
            </el-table>





            <el-pagination
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
            goDetailPage(id) {
                window.sessionStorage.setItem("itemId", id);
                this.$router.push("/goods/detail");
            },

            handleSizeChange(newSize) {
                this.queryInfo.pagesize = newSize;
                this.getGoodsList();
            },
            handleCurrentChange(newPage) {
                this.queryInfo.pagenum = newPage;
                this.getGoodsList();
            },
        },
        created() {
            this.getGoodsList();
        },

    };
</script>

<style lang="less" scoped></style>
