##eg
```
[
    {
        "key": "result",
        "size": 2
    },
    {
        "key": "workStatus",
        "size": 1
    },
    {
        "key": "len",
        "size": 2
    },
    {
        "key": "childArr",
        "size": 3,
        "step_count_key": "len",
        "childList": [
            {
                "key": "workStatus1",
                "size": 1
            },
            {
                "key": "workStatus2",
                "size": 1
            },
            {
                "key": "workStatus3",
                "size": 1
            }
        ]
    },
    {
        "key": "tail",
        "size": 5
    }
]

```
#intput:
```
 00000100020102030203012211334455
```
# output:
 -------decodeDic:
 
```

 {
    result=0,
    workStatus=1,
    len=2,
    tail=2211334455,
    childArr=[
        {
            workStatus3=3,
            workStatus2=2,
            workStatus1=1
        },
        {
            workStatus3=1,
            workStatus2=3,
            workStatus1=2
        }
    ]
}

```

#temple:

```
{
    "template": [
        {
            "key": "result",
            "size": 2
        },
        {
            "key": "workStatus",
            "size": 1
        },
        {
            "key": "len",
            "size": 2
        },
        {
            "key": "childArr",
            "size": 3,
            "step_count_key": "len",
            "childList": [
                {
                    "key": "workStatus1",
                    "size": 1
                },
                {
                    "key": "workStatus2",
                    "size": 1
                },
                {
                    "key": "workStatus3",
                    "size": 1
                }
            ]
        },
        {
            "key": "tail",
            "size": 5
        }
    ]
}
```