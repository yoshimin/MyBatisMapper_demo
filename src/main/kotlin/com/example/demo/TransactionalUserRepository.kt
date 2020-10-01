package com.example.demo

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class TransactionalUserRepository(private val userRepository: UserRepository) {
    @Transactional
    fun selectAndUpdate(id: Int, name: String, email: String): Int {
        val user = userRepository.selectForUpdate(id) ?: return 0
        return userRepository.updateWithCount(id, name, email, user.updateCount+1)
    }
}
