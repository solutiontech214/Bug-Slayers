import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";

export default function Chart({ data }) {

  const chartData = [
    { name: "INFO", value: data.INFO || 0 },
    { name: "WARN", value: data.WARN || 0 },
    { name: "ERROR", value: data.ERROR || 0 },
    { name: "FATAL", value: data.FATAL || 0 }
  ];

  const COLORS = ["#22c55e", "#facc15", "#ef4444", "#a855f7"];

  return (
    <div className="bg-gray-900 p-6 rounded-xl">
      <h2 className="text-lg mb-4">📊 Log Distribution</h2>

      <PieChart width={400} height={300}>
        <Pie
          data={chartData}
          dataKey="value"
          cx="50%"
          cy="50%"
          outerRadius={100}
        >
          {chartData.map((entry, index) => (
            <Cell key={index} fill={COLORS[index]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend />
      </PieChart>
    </div>
  );
}