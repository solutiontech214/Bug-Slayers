export default function Docs() {
  return (
    <div className="text-black p-8 max-w-5xl mx-auto">

      <h1 className="text-3xl font-bold mb-6">📄 Logger SDK Documentation</h1>

      {/* Overview */}
      <section className="mb-6">
        <h2 className="text-xl font-semibold mb-2">Overview</h2>
        <p>
          Logger SDK is a lightweight Java logging library used to send logs
          to a centralized backend system for monitoring and analysis.
        </p>
      </section>

      {/* Init */}
      <section className="mb-6">
        <h2 className="text-xl font-semibold  mb-2">Initialization</h2>

        <pre className="bg-gray-900 p-4 rounded-lg text-sm overflow-x-auto text-white">
{`SDKConfig config = new SDKConfig.Builder()
  .apiKey("sk_test_123")
  .projectId("payment-service")
  .moduleId("auth-module")
  .subModuleId("login-api")
  .environment("development")
  .endpoint("http://localhost:8080/logs")
  .batchSize(5)
  .flushIntervalSeconds(3)
  .build();

LoggerSDK.init(config);`}
        </pre>
      </section>

      {/* Methods */}
      <section className="mb-6">
        <h2 className="text-xl font-semibold mb-2">Log Methods</h2>

        <pre className="bg-gray-900 p-4 rounded-lg text-sm text-white">
{`LoggerSDK.info("App started");
LoggerSDK.warn("This is a warning");
LoggerSDK.error("Divide by zero error", e);`}
        </pre>
      </section>

      {/* Features */}
      <section className="mb-6">
        <h2 className="text-xl font-semibold mb-2">Features</h2>
        <ul className="list-disc ml-6">
          <li>Batch log processing</li>
          <li>Async logging</li>
          <li>Error tracking</li>
          <li>Structured metadata</li>
        </ul>
      </section>

      {/* Flow */}
      <section>
        <h2 className="text-xl font-semibold mb-2">How It Works</h2>
        <p>
          Application → LoggerSDK → Backend API → Database → Dashboard
        </p>
      </section>

    </div>
  );
}