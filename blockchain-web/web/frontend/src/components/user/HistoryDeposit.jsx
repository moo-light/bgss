import React, { useState, useEffect } from "react";
import { Input, Button, Table, Typography, Space } from "antd";
import { useGetHistoryDepositQuery } from "../../../src/redux/api/userApi";
import Loader from "../layout/Loader";
import { myDateFormat } from "../../helpers/helpers";
import MetaData from "../layout/MetaData";

const { Text } = Typography;
const { Search } = Input;

const ListHistoryDeposit = () => {
  const { data, isLoading } = useGetHistoryDepositQuery();
  const [searchTerm, setSearchTerm] = useState("");
  const [sortedData, setSortedData] = useState([]);
  const [pageSize, setPageSize] = useState(10);
  const [currentPage, setCurrentPage] = useState(1);
  useEffect(() => {
    if (data?.data) {
      // Sắp xếp dữ liệu ngay khi tải dữ liệu
      setSortedData([...data.data].sort((a, b) => a.id - b.id));
    }
  }, [data]);

  const handleChange = (event) => {
    setSearchTerm(event.target.value);
    if (!event.target.value) {
      setSortedData(data.data); // Reset to original data when search term is cleared
    }
  };

  const handleSubmit = () => {
    const results = sortedData.filter((transaction) =>
      transaction.orderCode.toLowerCase().includes(searchTerm.toLowerCase())
    );
    // Cần cập nhật lại sortedData, không phải searchTerm.
    setSortedData(searchTerm ? results : data?.data || []);
  };

  // Nếu muốn sắp xếp khi người dùng click vào header của bảng
  const handleTableChange = (pagination, filters, sorter) => {
    if (sorter.field && sorter.order) {
      const sortedList = [...sortedData];
      sortedList.sort((a, b) => {
        if (a[sorter.field] < b[sorter.field]) {
          return sorter.order === "ascend" ? -1 : 1;
        }
        if (a[sorter.field] > b[sorter.field]) {
          return sorter.order === "ascend" ? 1 : -1;
        }
        return 0;
      });
      setSortedData(sortedList);
    }
  };

  if (isLoading) return <Loader />;

  const transactions = searchTerm
    ? sortedData.filter((transaction) =>
        transaction.orderCode.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : sortedData;

  const columns = [
    { title: "ID", dataIndex: "id", sorter: true },
    { title: "Order Code", dataIndex: "orderCode", sorter: true },
    { title: "Amount", dataIndex: "amount", sorter: true },
    { title: "Bank Code", dataIndex: "bankCode" },
    {
      title: "Payment Status",
      dataIndex: "paymentStatus",
      render: (text) => (
        <Text
          strong={text === "SUCCESS"}
          type={text === "SUCCESS" ? "success" : "warning"}
        >
          {text}
        </Text>
      ),
    },
    {
      title: "Pay Date",
      dataIndex: "payDate",
      render: (date) => myDateFormat(new Date(date)),
      sorter: true,
    },
    // { title: "Balance", dataIndex: "balance", sorter: true },
  ];

  return (
    <>
      <MetaData title={"Deposit History"} />

      <h1 className="my-5 px-3">
        {`${transactions?.length || 0} Deposit Transactions`}
      </h1>
      <div
        className="mb-2 px-3"
        style={{ display: "flex", justifyContent: "space-between" }}
      >
        <Search
          placeholder="Search by Order Code"
          value={searchTerm}
          onChange={handleChange}
          onSearch={handleSubmit}
          style={{ width: 200 }}
        />
      </div>
      <div className="px-3">
        <Table
          columns={columns}
          dataSource={transactions}
          bordered
          onChange={handleTableChange}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: transactions.length,
            showSizeChanger: true,
            onShowSizeChange: (current, size) => setPageSize(size),
            onChange: (page) => setCurrentPage(page),
          }}
        />
      </div>
    </>
  );
};

export default ListHistoryDeposit;
