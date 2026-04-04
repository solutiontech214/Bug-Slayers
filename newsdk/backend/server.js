const express = require("express");
const app = express();

app.use(express.json());

// 🔥 Simulated DB (in-memory)
const logs = [];

app.post("/logs", (req, res) => {
  const apiKey = req.headers.authorization;

  console.log("\n🔥 Incoming Request");
  console.log("API Key:", apiKey);

  const data = req.body;

  // Handle batch logs
  if (Array.isArray(data)) {
    data.forEach(log => logs.push(log));
  } else {
    logs.push(data);
  }

  console.log("📦 Stored Logs:", logs.length);

  // Print latest logs
  console.log("🧾 Logs Received:");
  console.dir(data, { depth: null });

  res.status(200).json({ message: "Logs received" });
});

// 🔥 Fetch logs (for dashboard)
app.get("/logs", (req, res) => {
  res.json(logs);
});

app.listen(8080, () => {
  console.log("🚀 Server running on http://localhost:8080");
});