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

const val postInvalidLogin = """
    {
        "user": {
            "email": "test@gmail.com"
        }
    }
"""

const val postNonExistentUserLogin = """
    {
        "user": {
            "email": "test123@gmail.com",
            "password": "test"
        }
    }
"""

const val postValidLogin = """
    {
        "user": {
            "email": "$",
            "password": "test"
        }
    }
"""

const val postInvalidCredentialsLogin = """
    {
        "user": {
            "email": "$",
            "password": "pass123"
        }
    }
"""

const val putUser = """
    {
        "user": {
            "bio": "Self Compassion", 
            "image":"test"
        }
    }
"""

const val putInvalidUser = """
    {
      "bio": "Self Compassion", 
      "image":"test"   
    }
"""