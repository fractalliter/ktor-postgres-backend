package com.personia.dao.user

import com.personia.models.User
import com.personia.models.Users
import org.jetbrains.exposed.sql.*
import com.personia.dao.DatabaseFactory.dbQuery

class UserDaoImpl: UserDaoFacade {

    private fun resultRowToNode(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        password = row[Users.password],
    )

    override suspend fun createUser(user: User): User? = dbQuery{
        val insertStatement = Users.insert {
            it[Users.username] = user.username
            it[Users.password] = user.password
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNode)
    }

    override suspend fun findByUsername(username: String): User? = dbQuery {
        Users
            .select { Users.username eq username }
            .map(::resultRowToNode).singleOrNull()
    }
}

val userDAO = UserDaoImpl()