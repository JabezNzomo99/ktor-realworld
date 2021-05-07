package com.example.users

const val postInvalidUser = """
    {
        "user": {
            "email": "test@gmail.com",
            "password": "test"
        }
    }
"""

const val postUser = """
    {
        "user": {
            "email": "test@gmail.com",
            "password": "test",
            "username": "test"
        }
    }
"""