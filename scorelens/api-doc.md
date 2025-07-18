### 🔸 Example JSON for InformationRequest
```json
{
  "code": "START_STREAM",
  "tableID": "23374e21-2391-41b0-b275-651df88b3b04",
  "modeID": 2,
  "data": {
    "cameraUrl": "C:/Users/ADMIN/Downloads/one_round.mp4",
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

```

### 🔸 Example JSON for ProducerRequest
```json

{
  "code": "LOGGING",
  "tableID": "23374e21-2391-41b0-b275-651df88b3b04",
  "data": {
    "cueBallId": 0,
    "targetBallId": 9,
    "modeID": 2,
    "message": "",
    "details": {
      "playerID": 290,
      "gameSetID": 242,
      "scoreValue": true,
      "isFoul": false,
      "isUncertain": false,
      "message": "No foul"
    }
  }
}
```






