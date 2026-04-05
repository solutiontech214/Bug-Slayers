export default function AISummary({ logs }) {

  const total = logs.length;
  const errors = logs.filter(l => l.level === "ERROR").length;
  const warns = logs.filter(l => l.level === "WARN").length;

  const errorRate = ((errors / total) * 100).toFixed(2);

  return (
    <div className="bg-gray-900 p-6 rounded-xl">
      <h2 className="text-lg mb-4">🤖 AI Log Analysis</h2>

      <p>Total Logs: {total}</p>
      <p>Errors: {errors}</p>
      <p>Warnings: {warns}</p>
      <p>Error Rate: {errorRate}%</p>

      <div className="mt-4 p-4 bg-gray-800 rounded-lg">
        {errorRate > 20
          ? "⚠️ High error rate detected! Immediate attention required."
          : "✅ System is stable with low error rate."}
      </div>
    </div>
  );
}