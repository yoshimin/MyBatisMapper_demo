package com.example.demo

import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

@RestController
@RequestMapping("/test/user")
class UserController(private val repository: UserRepository,
                     private val transactionalRepository: TransactionalUserRepository) {
    @PostMapping("/add")
    fun add(@RequestBody request: AddUserRequest): String {
        repository.insert(request)
        return "success! userId = ${request.id}"
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): String {
        val user = repository.select(id) ?: return "no user! userId = $id"
        return user.toString()
    }

    @PostMapping("/update/{id}")
    fun update(@PathVariable id: Int, @RequestBody request: UpdateUserRequest): String {
        val id = repository.update(id, request.name, request.email)
        return "success! userId = $id"
    }

    @PostMapping("/select-and-update/{id}")
    fun selectAndUpdate(@PathVariable id: Int, @RequestBody request: UpdateUserRequest): String {
        val id = transactionalRepository.selectAndUpdate(id, request.name, request.email)
        return "success! userId = $id"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Int): String {
        val count = repository.delete(id)
        if (count == 0) {
            return "no user! userId = $id"
        }
        return "success!"
    }


    val scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(10))

    @PostMapping("/add-async")
    fun addAsync(@RequestBody request: AddUserRequest): Mono<String> {
        return Mono
                .fromCallable {
                    repository.insert(request)
                    "success! userId = ${request.id}"
                }
                .subscribeOn(scheduler)
                .onErrorResume(DuplicateKeyException::class.java) {
                    Mono.just("\"${request.name}\" already exists!")
                }
    }
}
