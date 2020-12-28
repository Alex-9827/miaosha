<template>
  <div>
    <h3>Hello，{{username}}</h3>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
    };
  },
  async created() {
    let params = new URLSearchParams();
    params.append("token", window.sessionStorage.getItem("token"))
    const { data:res } = await this.$http.post("/user/currentUser", params);

    if (res.status === "success") {
      this.username = res.data.name;
    }
    else{
      this.$message({
        type: "error",
        message: res.msg
      });
    }
  }
}
</script>

<style lang="less" scoped>

</style>