package com.example.tags

class Controller(private val repository: Repository) {
    suspend fun getAll() = repository.getAll()
}