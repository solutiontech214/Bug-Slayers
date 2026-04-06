import { useEffect, useState } from "react";

function App() {
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/dashboard/logs", {
      headers: {
        Authorization: "Bearer sk_admin"
      }
    })
      .then(res => res.json())
      .then(data => {
        console.log("Response:", data);

        // ✅ FIX: ensure it's array
        if (Array.isArray(data)) {
          setLogs(data);
        } else {
          console.error("Not an array:", data);
          setLogs([]); // prevent crash
        }
      })
      .catch(err => console.error(err));
  }, []);

  return (
    <div>
      <h1>Logs</h1>

      {logs.length === 0 ? (
        <p>No logs found</p>
      ) : (
        logs.map((log, i) => (
          <div key={i}>
            {log.level} - {log.message}
          </div>
        ))
      )}
    </div>
  );
}

export default App;