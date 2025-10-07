#!/bin/bash
set -e
BASE="http://localhost:8080"

echo "1) List all alerts (admin):"
curl -s $BASE/alerts | jq

echo "\n2) Create sample alert (admin):"
CREATE=$(curl -s -X POST $BASE/alerts -H "Content-Type: application/json" -d '{
  "title":"Demo Alert",
  "message":"This is a demo",
  "severity":"INFO",
  "startTime":"2025-10-01T00:00:00",
  "expiryTime":"2025-10-10T00:00:00",
  "visibilityType":"ORGANIZATION"
}')
echo $CREATE | jq

echo "\n3) Fetch active alerts for user1:"
curl -s $BASE/users/user1/alerts | jq

echo "\n4) Trigger reminders (manual):"
curl -s -X POST $BASE/trigger-reminders | jq

echo "\n5) Snooze an alert for user1 (pick first alert from GET):"
ALERT_ID=$(curl -s $BASE/users/user1/alerts | jq -r '.[0].alert_id')
echo "Alert ID: $ALERT_ID"
curl -s -X POST $BASE/users/user1/alerts/$ALERT_ID/snooze | jq

echo "\n6) Get snoozed alerts for user1:"
curl -s $BASE/users/user1/alerts/snoozed | jq

echo "\n7) Mark alert unread for user1:"
curl -s -X PATCH $BASE/users/user1/alerts/$ALERT_ID/unread | jq

echo "\n8) Get analytics:"
curl -s $BASE/analytics/alerts | jq
