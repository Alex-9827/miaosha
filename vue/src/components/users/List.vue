<template>
    <div>
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>用户管理</el-breadcrumb-item>
            <el-breadcrumb-item>用户列表</el-breadcrumb-item>
        </el-breadcrumb>

        <el-card>
            <el-row :gutter="20">
                <el-col :span="8">
                    <el-input
                            placeholder="请输入内容"
                            clearable
                            v-model="query"
                            @clear="getUsersList"
                    >
                        <el-button
                                slot="append"
                                icon="el-icon-search"
                                @click="handleCurrentChange(1);getUsersList;"
                        ></el-button>
                    </el-input>
                </el-col>
            </el-row>

            <el-table :data="userslist" stripe border style="width: 100%">
                <el-table-column type="index"> </el-table-column>
                <el-table-column prop="name" label="姓名"></el-table-column>
                <el-table-column
                        prop="gender"
                        label="性别"
                        width="95px"
                ></el-table-column>
                <el-table-column
                        prop="age"
                        label="年龄"
                        width="70px"
                ></el-table-column>
                <el-table-column prop="telephone" label="手机号"></el-table-column>
                <el-table-column prop="role" label="身份"></el-table-column>

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
                userslist: [],
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
            async getUsersList() {
                let params = new URLSearchParams();
                params.append("name", this.query)
                params.append("token", window.sessionStorage.getItem("token"))
                const { data:res } = await this.$http.post("/user/selectByName", params);
                console.log(res)
                if (res.status !== "success") {
                    return this.$message.error(res.data);
                }
                var list = res.data
                for(var i = 0; i < list.length; i ++){
                    if(list[i].gender == 1) list[i].gender = "男";
                    else list[i].gender = "女";
                }
                this.userslist = res.data;
                this.total = this.userslist.length;
            },
            handleSizeChange(newSize) {
                this.queryInfo.pagesize = newSize;
                this.getUsersList();
            },
            handleCurrentChange(newPage) {
                this.queryInfo.pagenum = newPage;
                this.getUsersList();
            },

        },
        created() {
            this.getUsersList();
        },

    };
</script>

<style lang="less" scoped></style>
