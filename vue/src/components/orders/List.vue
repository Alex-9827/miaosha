<template>
    <div>
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>订单管理</el-breadcrumb-item>
            <el-breadcrumb-item>订单列表</el-breadcrumb-item>
        </el-breadcrumb>

        <el-card>
            <el-row :gutter="20">
                <el-col :span="8">
                    <el-input
                            placeholder="请输入内容"
                            clearable
                            v-model="query"
                            @clear="getOrdersList"
                    >
                        <el-button
                                slot="append"
                                icon="el-icon-search"
                                @click="handleCurrentChange(1);getOrdersList;"
                        ></el-button>
                    </el-input>
                </el-col>
            </el-row>

            <el-table :data="orderslist" stripe border style="width: 100%">
                <el-table-column type="index"> </el-table-column>
                <el-table-column prop="id" label="订单ID"></el-table-column>
                <el-table-column prop="itemId" label="商品ID"></el-table-column>
                <el-table-column prop="userId" label="用户ID"></el-table-column>
                <el-table-column prop="promoId" label="活动ID"></el-table-column>
                <el-table-column prop="itemPrice" label="商品单价"></el-table-column>
                <el-table-column prop="orderPrice" label="订单总价"></el-table-column>
                <el-table-column prop="amount" label="数量"></el-table-column>

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
                orderslist: [],
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
            async getOrdersList() {
                let params = new URLSearchParams();
                params.append("name", this.query)
                params.append("token", window.sessionStorage.getItem("token"))
                const { data:res } = await this.$http.post("/order/selectByName", params);
                console.log(res)
                if (res.status !== "success") {
                    return this.$message.error(res.data);
                }
                this.orderslist = res.data;
                this.total = this.orderslist.length;
            },
            handleSizeChange(newSize) {
                this.queryInfo.pagesize = newSize;
                this.getOrdersList();
            },
            handleCurrentChange(newPage) {
                this.queryInfo.pagenum = newPage;
                this.getOrdersList();
            },

        },
        created() {
            this.getOrdersList();
        },

    };
</script>

<style lang="less" scoped></style>
