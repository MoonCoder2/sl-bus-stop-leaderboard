import React, { useState, useEffect } from "react";
import { Table, Button, Alert } from "antd";
import {ReloadOutlined} from "@ant-design/icons"
const columns = [
  {
    title: "Bus Number",
    dataIndex: "busNumber",
    key: "busNumber",
  },
  {
    title: "Number of Bus Stops",
    dataIndex: "stops",
    key: "numBusStops",
    render: (stops) => stops.length,
  },
  {
    title: "Bus Stops",
    dataIndex: "stops",
    key: "busStopNames",
    render: (stops, record) => (
        <div style={{ height: "80px", overflowY: "auto" }}>
        <ul style={{paddingInlineStart:5}}>
        {stops.map((stop, i) => {
          //console.log(`${record.busNumber}-${i}-${busStopName}`);
          return <li key={`${record.busNumber}-${i}`}>{stop.id}:{stop.name}</li>
          })}
        
      </ul>
      </div>
    ),
  },
];

const MostBusStops = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      const result = await fetch("/api/bus-lines/most-bus-stops");
      if (result.ok) {
        const data = await result.json();
        setData(data);
        setError(null);
      } else {
        setError("Error fetching data. Please try again later.");
      }
    } catch (err) {
      setError("Error fetching data. Please try again later.");
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <>
      <Button onClick={fetchData} style={{margin: 10}}> <ReloadOutlined/> Refresh</Button>
      {error && <Alert message={error} type="error" />}
      <Table columns={columns} dataSource={data} loading={loading} rowKey='busNumber'/>
    </>
  );
};

export default MostBusStops;