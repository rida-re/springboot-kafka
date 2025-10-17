@echo off
echo ==========================================
echo Testing Kafka Connect with Built-in Connector
echo ==========================================
echo.

REM Test 1: List available connector plugins
echo 1. Listing available connector plugins...
curl -s http://localhost:8083/connector-plugins
echo.
echo.

REM Test 2: Create a FileStreamSource connector
echo 2. Creating FileStreamSource connector...
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d "{\"name\": \"test-file-source\", \"config\": {\"connector.class\": \"org.apache.kafka.connect.file.FileStreamSourceConnector\", \"tasks.max\": \"1\", \"file\": \"/tmp/test-input.txt\", \"topic\": \"test-file-topic\"}}"
echo.
echo.

REM Test 3: Check connector status
echo 3. Checking connector status...
timeout /t 2 /nobreak >nul
curl -s http://localhost:8083/connectors/test-file-source/status
echo.
echo.

REM Test 4: List all connectors
echo 4. Listing all connectors...
curl -s http://localhost:8083/connectors
echo.
echo.

echo ==========================================
echo Test Complete!
echo ==========================================
echo.
echo To delete the test connector, run:
echo   curl -X DELETE http://localhost:8083/connectors/test-file-source
echo.
pause
