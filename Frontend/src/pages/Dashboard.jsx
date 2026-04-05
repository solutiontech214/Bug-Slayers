import { useEffect, useState } from "react";

function Dashboard() {
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/dashboard/logs", {
      headers: {
        Authorization: "Bearer sk_admin"
      }
    })
      .then(res => res.json())
      .then(data => setLogs(data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div style={{ padding: "20px" }}>
      <h1>🚀 Logs Dashboard</h1>

      <table border="1" cellPadding="10">
        <thead>
          <tr>
            <th>Message</th>
            <th>Level</th>
            <th>Project</th>
            <th>Module</th>
          </tr>
        </thead>

        <tbody>
          {logs.map((log, index) => (
            <tr key={index}>
              <td>{log.message}</td>
              <td>{log.level}</td>
              <td>{log.projectId}</td>
              <td>{log.moduleId}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Dashboard;