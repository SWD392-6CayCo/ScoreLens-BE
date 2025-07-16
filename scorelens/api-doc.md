### 🔸 Example JSON for InformationRequest
```json
{
  "code": "START_STREAM",
  "tableID": "23374e21-2391-41b0-b275-651df88b3b04",
  "modeID": 2,
  "data": {
    "cameraUrl": "rtsp://192.168.1.100/stream",
    "totalSet": 1,
    "sets": [
      {
        "gameSetID": 1,
        "raceTo": 1
      }
    ],
    "teams": [
      {
        "teamID": 0,
        "players": [
          {
            "playerID": 0,
            "name": "Player A"
          }
        ]
      },
      {
        "teamID": 1,
        "players": [
          {
            "playerID": 2,
            "name": "Player C"
          }
        ]
      }
    ]
  }
}


