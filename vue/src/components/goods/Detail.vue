<template>
    <div>
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>商品管理</el-breadcrumb-item>
            <el-breadcrumb-item>商品详情</el-breadcrumb-item>
        </el-breadcrumb>
        <el-card style="width: 100%">
            <img :src="good.imgUrl" width="35%" height="400">
            <div style="float: right; width: 45%; height: 400px">
                <div style="float: left; width: 100%;">
                    <div class="show_button">商品名称：{{good.title}}</div>
                    <div class="show_button">商品价格：{{good.price}}</div>
                    <div class="show_button">活动价格：{{promo.promoItemPrice}}</div>
                    <div class="show_button">商品库存：{{good.stock}}</div>
                    <div class="show_button">商品销量：{{good.sales}}</div>
                    <div class="show_button">商品描述：{{good.description}}</div>
                    <div class="show_button">活动时间：{{promo.startDate}} - {{promo.endDate}}</div>
                </div>
                <el-button type="primary" @click="getsecKillToken()">立即购买</el-button>
            </div>
        </el-card>


    </div>
</template>

<script>
    export default {
        data() {
            return {
                good: [],
                promo: [],
                amount: 1,
                secKillToken: '',
            };
        },
        methods: {
            async getGood() {
                let params = new URLSearchParams();
                params.append("id", Number(window.sessionStorage.getItem("itemId")))
                params.append("token", window.sessionStorage.getItem("token"))
                const { data:res } = await this.$http.post("/item/get", params);
                console.log(res)
                if (res.status !== "success") {
                    return this.$message.error(res.data);
                }
                this.good = res.data;
                console.log(this.good.imgUrl)
            },
            async getPromo() {
                let params = new URLSearchParams();
                params.append("itemId", Number(window.sessionStorage.getItem("itemId")))
                params.append("token", window.sessionStorage.getItem("token"))
                const { data:res } = await this.$http.post("/promo/select", params);
                console.log(res)
                if (res.status !== "success") {
                    return this.$message.error(res.data);
                }
                this.promo = res.data
                console.log(this.promo)
                var d = new Date(res.data.startDate.millis)
                this.promo.startDate = d.toDateString();
                d = new Date(res.data.endDate.millis)
                this.promo.endDate = d.toDateString();

            },
            async getsecKillToken(){
                let params = new URLSearchParams();
                params.append("itemId", Number(window.sessionStorage.getItem("itemId")))
                params.append("promoId", this.promo.id)
                params.append("token", window.sessionStorage.getItem("token"))
                const { data:res } = await this.$http.post("/order/secKillToken", params);
                if (res.status !== "success") {
                    return this.$message.error("下单失败");
                }
                this.secKillToken = res.data
                console.log(this.secKillToken)
                this.createOrder();

            },
            async createOrder(){

                let params = new URLSearchParams();
                params.append("itemId", Number(window.sessionStorage.getItem("itemId")))
                params.append("amount", this.amount)
                params.append("promoId", this.promo.id)
                params.append("token", window.sessionStorage.getItem("token"))
                params.append("secKillToken", this.secKillToken)
                const { data:res } = await this.$http.post("/order/createOrder", params);
                console.log(res)
                if (res.status !== "success") {
                    return this.$message.error(res.data);
                }
                if (res.status == "success") {
                    this.getGood();
                    return this.$message.success(res.data);
                }



            },


        },
        created() {
            this.getGood();
            this.getPromo();
        },

    };
</script>

<style lang="less" scoped>
    .show_button{
        margin-top: 20px;
        margin-bottom: 10px;
        font-family: "微软雅黑";
    }
</style>
