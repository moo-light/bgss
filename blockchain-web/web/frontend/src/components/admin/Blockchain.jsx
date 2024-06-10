import { MDBDataTable } from "mdbreact";
import React from "react";
import MetaData from "../layout/MetaData";

const Blockchain = () => {
  // Dữ liệu cho bảng MDBDataTable
  const data = {
    columns: [
      {
        label: "Blocks",
        field: "blocks",
        sort: "asc",
      },
      {
        label: "Transactions",
        field: "transactions",
        sort: "asc",
      },
      {
        label: "Network",
        field: "network",
        sort: "asc",
      },
      {
        label: "Difficulty",
        field: "difficulty",
        sort: "asc",
      },
      {
        label: "Last Block Hash",
        field: "lastBlockHash",
        sort: "asc",
      },
    ],
    rows: [
      {
        blocks: 1000,
        transactions: 5000,
        network: "Mainnet",
        difficulty: "Hard",
        lastBlockHash: "0x123456789abcdef",
      },
      {
        blocks: 1020,
        transactions: 5000,
        network: "Mainnet",
        difficulty: "Hard",
        lastBlockHash: "0x123456789abcdef",
      },
      {
        blocks: 1000,
        transactions: 5000,
        network: "Mainnet",
        difficulty: "Hard",
        lastBlockHash: "0x123456789abcdef",
      },
      {
        blocks: 1000,
        transactions: 5000,
        network: "Mainnet",
        difficulty: "Hard",
        lastBlockHash: "0x123456789abcdef",
      },
    ],
  };

  return (
    <div className="container" style={{ background: "#FFD700" }}>
      <MetaData title="Blockchain Information" />
      <h1 className="text-center mt-5" style={{ color: "red" }}>
        Blockchain Information
      </h1>
      <MDBDataTable
        className="mt-4"
        hover
        responsive
        bordered
        data={data}
        displayEntries={true}
        searching={true}
        paging={true}
      />
      <style jsx>{`
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter,
        .dataTables_wrapper .dataTables_info,
        .dataTables_wrapper .dataTables_paginate {
          margin-bottom: 20px; /* Điều chỉnh giá trị theo ý muốn của bạn */
        }
      `}</style>
    </div>
  );
};

export default Blockchain;
