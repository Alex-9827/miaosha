<template>
  <el-container style="height:100vh">
    <el-header>
      <div>
        <img src="../assets/logo.png" alt />
        <span>秒杀系统</span>
      </div>
      <el-button type="info" @click="logout">退出</el-button>
    </el-header>
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '200px'">
        <div class="toggle-button" @click="toggleCollapse">|||</div>
        <el-menu
          router
          background-color="#333744"
          text-color="#fff"
          active-text-color="#409EFF"
          unique-opened
          :collapse="isCollapse"
          :collapse-transition="false"
          :default-active="$route.path"
        >
          <el-submenu :index="item.id+''" v-for="item in menulist" :key="item.id">
            <template slot="title">
              <i :class="iconObj[item.id]"></i>
              <span>{{item.authName}}</span>
            </template>
            <el-menu-item :index="'/'+subItem.path" v-for="subItem in item.children" :key="subItem.id">
              <template slot="title">
                <i class="el-icon-menu"></i>
                <span>{{subItem.authName}}</span>
              </template>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  data() {
    return {
      menulist: [],
      userMenulist: [
        {"id":0,"authName":"个人信息","path":"","children":[{"id":100,"authName":"我的信息","path":"user","children":[],"order":null}, {"id":104,"authName":"历史订单","path":"orders","children":[],"order":null}],"order":1},
        {"id":5,"authName":"秒杀商城","path":"","children":[{"id":105,"authName":"商品列表","path":"goods/show","children":[],"order":null}],"order":2},
      ],
      administartorMenulist: [
        {"id":0,"authName":"个人信息","path":"","children":[{"id":100,"authName":"我的信息","path":"user","children":[],"order":null}],"order":0},
        {"id":1,"authName":"用户管理","path":"","children":[{"id":101,"authName":"用户列表","path":"users/list","children":[],"order":null}],"order":1},
        {"id":2,"authName":"商品管理","path":"","children":[{"id":102,"authName":"商品列表","path":"goods","children":[],"order":null}],"order":2},
        {"id":3,"authName":"订单管理","path":"","children":[{"id":103,"authName":"订单列表","path":"orders/list","children":[],"order":null},],"order":3},
        {"id":4,"authName":"活动管理","path":"","children":[{"id":104,"authName":"活动列表","path":"promos","children":[],"order":null},],"order":4},
      ],
      iconObj: {
        "125": "iconfont icon-icon_user",
        "103": "iconfont icon-tijikongjian",
        "101": "iconfont icon-shangpin",
        "102": "iconfont icon-danju",
        "145": "iconfont icon-baobiao"
      },
      isCollapse: false,
    };
  },
  methods: {
    logout() {
      window.sessionStorage.clear;
      this.$router.push("/login");
    },
    async getMenuList() {
      let params = new URLSearchParams();
      params.append("token", window.sessionStorage.getItem("token"))
      const { data:res } = await this.$http.post("/user/currentUser", params);
      if (res.status === "success") {
        var role = res.data.role;
        if(role == "用户"){
          this.menulist = this.userMenulist;
        }
        else if(role == "管理员") {
          this.menulist = this.administartorMenulist;
        }
      } else {
        this.$message({
          type: "error",
          message: res.msg
        });
      }
    },
    toggleCollapse() {
      this.isCollapse = !this.isCollapse;
    }
  },
  created() {
    this.getMenuList();
  }
};
</script>

<style lang="less" scoped>
.el-header {
  background-color: #373d41;
  display: flex;
  justify-content: space-between;
  padding-left: 0;
  align-items: center;
  color: #fff;
  font-size: 20px;
  > div {
    display: flex;
    align-items: center;
    span {
      margin-left: 15px;
    }
  }
  img {
    width: 50px;
    height: 50px;
  }
}

.el-aside {
  background-color: #333744;
  .el-menu {
    border-right: none;
  }
}

.el-main {
  background-color: #eaedf1;
}

.iconfont {
  margin-right: 10px;
}

.toggle-button {
  background-color: #4a5064;
  font-size: 10px;
  line-height: 24px;
  color: #fff;
  text-align: center;
  letter-spacing: 0.2em;
  cursor: pointer;
}
</style>