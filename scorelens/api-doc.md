### 🔸 Example JSON for InformationRequest
```json
{
  "code": "START_STREAM",
  "data": {
    "cameraUrl": "rtsp://192.168.1.100/stream",
    "sets": [
      {
        "gameSetID": 1
      },
      {
        "gameSetID": 2
      }
    ],
    "teams": [
      {
        "teamID": 0,
        "players": [
          {
            "playerID": 0,
            "name": "Player A"
          },
          {
            "playerID": 1,
            "name": "Player B"
          }
        ]
      },
      {
        "teamID": 1,
        "players": [
          {
            "playerID": 2,
            "name": "Player C"
          },
          {
            "playerID": 3,
            "name": "Player D"
          }
        ]
      }
    ]
  }
}


