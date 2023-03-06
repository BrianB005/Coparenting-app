package com.serah.coparenting.retrofit


interface ExpensesInterface {
    fun success(expenses:List<Expense>)
    fun failure(throwable: Throwable)
    fun errorExists(message:String)
}