# Create Procfile with the required line (keeps $PORT literal)
Set-Content -Path Procfile -Value 'web: java -Dspring.profiles.active=prod -Dserver.port=$PORT -jar target/*.jar' -Encoding UTF8

# Verify contents
Get-Content Procfile

