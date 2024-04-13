package com.personia.dao.user

import com.personia.dao.DatabaseFactory.dbQuery
import com.personia.models.User
import com.personia.models.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserDaoImpl : UserDaoFacade {

    private fun resultRowToNode(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        password = row[Users.password],
    )

    override suspend fun createUser(user: User): User? = dbQuery {
        val insertStatement = Users.insert {
            it[username] = user.username
            it[password] = user.password
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