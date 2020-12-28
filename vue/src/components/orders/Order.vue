<template>
    <div>
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>个人信息</el-breadcrumb-item>
            <el-breadcrumb-item>历史订单</el-breadcrumb-item>
        </el-breadcrumb>
        <el-card>
            <el-table :data="orders" border stripe style="width: 100%">
                <el-table-column prop="id" label="订单ID"></el-table-column>
                <el-table-column prop="itemId" label="商品ID"></el-table-column>
                <el-table-column prop="itemPrice" label="商品价格"></el-table-column>
                <el-table-column prop="amount" label="商品数量"></el-table-column>
                <el-table-column prop="orderPrice" label="订单总价"></el-table-column>
            </el-table>
        </el-card>

    </div>
</template>

<script>
    export default {
        data() {
            return {
                orders: [],
            };
        },
        methods: {
            async getOrders() {
                let params = new URLSearchParams();
                params.append("token", window.sessionStorage.getItem("token"))
                const { data: res } = await this.$http.post("/order/get", params);
                this.orders = res.data;
                console.log(res)
            },
        },
        created() {
            this.getOrders();
        }
    };
</script>

<style lang="less" scoped></style>
