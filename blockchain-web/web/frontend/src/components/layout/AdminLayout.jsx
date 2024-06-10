import {
  AppstoreOutlined,
  ContainerOutlined,
  DashboardOutlined,
  FileDoneOutlined,
  ProfileOutlined,
  ShoppingOutlined,
  StarOutlined,
  UserOutlined
} from "@ant-design/icons";
import { Layout, Menu } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import Loader from "./Loader";
import MetaData from "./MetaData";
const { Header, Content, Sider } = Layout;

const AdminLayout = ({ children }) => {
  const { roles } = useSelector((state) => state.auth);

  const menuItemsAdmin = [
    {
      name: "Dashboard", 
      url: "/admin/dashboard",
      icon: <DashboardOutlined />,
    },
    {
      name: "CategoryProducts",
      url: "/admin/categoryProducts",
      icon: <AppstoreOutlined />,
    },
    {
      name: "CategoryPosts",
      url: "/admin/categoryPosts",
      icon: <AppstoreOutlined />,
    },
    {
      name: "TypeGolds",
      url: "/admin/typeGolds",
      icon: <AppstoreOutlined />,
    },

    {
      name: "Products",
      url: "/admin/products",
      icon: <ContainerOutlined />,
    },
   
    {
      name: "Posts",
      url: "/admin/posts",
      icon: <ProfileOutlined />,
    },
    {
      name: "Discounts",
      url: "/admin/discounts",
      icon: <ShoppingOutlined />,
    },
    {
      name: "Orders",
      url: "/admin/orders",
      icon: <FileDoneOutlined />,
    },
    {
      name: "Users",
      url: "/admin/users",
      icon: <UserOutlined />,
    },
    {
      name: "Reviews",
      url: "/admin/reviews",
      icon: <StarOutlined />,
    },
    // {
    //   name: "Weights Convert",
    //   url: "/admin/weight-conversions",
    //   icon: <GoldOutlined />,
    // },
  ];

  const menuItemsStaff = [
    {
      name: "Dashboard",
      url: "/admin/dashboard",
      icon: <DashboardOutlined />,
    },
    {
      name: "CategoryProducts",
      url: "/admin/categoryProducts",
      icon: <AppstoreOutlined />,
    },
    {
      name: "CategoryPosts",
      url: "/admin/categoryPosts",
      icon: <AppstoreOutlined />,
    },
    {
      name: "Products",
      url: "/admin/products",
      icon: <ContainerOutlined />,
    },
    {
      name: "Posts",
      url: "/admin/posts",
      icon: <ProfileOutlined />,
    },
    {
      name: "Orders",
      url: "/admin/orders",
      icon: <FileDoneOutlined />,
    },
    {
      name: "Reviews",
      url: "/admin/reviews",
      icon: <StarOutlined />,
    },
  ];

  const selectedMenuItems = roles.includes("ROLE_ADMIN")
    ? menuItemsAdmin
    : menuItemsStaff;

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider style={{ background: "#ffff" }}>
        <div className="logo" />
        <Menu theme="light" mode="inline">
          {selectedMenuItems.map((item, index) => (
            <Menu.Item key={index} icon={item.icon}>
              <Link
                to={item.url}
                style={{ textDecoration: "none", fontWeight: "bold" }}
              >
                {item.name}
              </Link>
            </Menu.Item>
          ))}
        </Menu>
      </Sider>
      <Layout className="site-layout">
        {/* <Header className="site-layout-background" style={{ padding: 0 }} /> */}
        <Content>
          <div className="site-layout-background" style={{ padding: 20 }}>
            {children}
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export function AdminLayoutLoader({ title }) {
  return (
    <>
      <AdminLayout>
        <MetaData title={title} />
        <Loader />
      </AdminLayout>
    </>
  );
}

export default AdminLayout;
