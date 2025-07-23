#!/bin/sh

LOG_FILE="/app/logs/app.log"
LAST_LINE_FILE="/app/.last_line"
SLACK_WEBHOOK="$SLACK_WEBHOOK_LOG"

if [ ! -f "$LAST_LINE_FILE" ]; then
  echo 0 > "$LAST_LINE_FILE"
fi

while true; do
  LAST_LINE=$(cat "$LAST_LINE_FILE")
  CURRENT_LINE=$(wc -l < "$LOG_FILE")

  if [ "$CURRENT_LINE" -gt "$LAST_LINE" ]; then
    tail -n +"$(($LAST_LINE + 1))" "$LOG_FILE" | while read -r line; do
      echo "$line" | grep -q "ERROR"
      if [ $? -eq 0 ]; then
        safe_line=$(echo "$line" | sed 's/\\/\\\\/g' | sed 's/"/\\"/g')
        curl -s -X POST -H 'Content-type: application/json' \
          --data "{\"text\": \"🚨 ERROR 로그 감지됨:\n${safe_line}\"}" "$SLACK_WEBHOOK"
      fi
    done
    echo "$CURRENT_LINE" > "$LAST_LINE_FILE"
  fi

  sleep 10
done
