<template>
  <div>
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>个人信息</el-breadcrumb-item>
      <el-breadcrumb-item>我的信息</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card style="width: 30%; text-align: center; margin-left: 30%; margin-top: 8%" >
      <img :src="src" width="100" height="100">
        <div style="float: left; width: 100%;">
          <div class="show_button">姓名：{{user.name}}</div>
          <div class="show_button">身份：{{user.role}}</div>
          <div class="show_button">年龄：{{user.age}}</div>
          <div class="show_button">性别：{{user.gender}}</div>
          <div class="show_button">手机号：{{user.telephone}}</div>
        </div>
    </el-card>


  </div>
</template>

<script>
  export default {
    data() {
      return {
        user: [],
        //src: 'https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3351113425,3755484207&fm=26&gp=0.jpg',
        src: 'https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1207533529,3946480795&fm=26&gp=0.jpg'
      };
    },
    methods: {
      async getUser() {
        let params = new URLSearchParams();
        params.append("token", window.sessionStorage.getItem("token"))
        const { data:res } = await this.$http.post("/user/currentUser", params);
        console.log(res)
        if (res.status !== "success") {
          return this.$message.error(res.data);
        }
        this.user = res.data;
        if(this.user.gender == 1) this.user.gender = "男"
        else this.user.gender = "女"
        console.log(this.user)
      },


    },
    created() {
      this.getUser();
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