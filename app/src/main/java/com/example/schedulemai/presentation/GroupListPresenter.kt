package com.example.schedulemai.presentation

import com.example.schedulemai.remote.HtmlParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter


/**
 * Created by Andrey Morgunov on 04/03/2021.
 */

class GroupListPresenter : MvpPresenter<GroupListView>() {

    private val compositeDisposable = CompositeDisposable()

    fun getGroups() {
        viewState.switchProgressBar()
        compositeDisposable.add(HtmlParser.parseGroups()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { groups ->
                    viewState.switchProgressBar(false)
                    viewState.onSuccess(groups)
                },
                { error -> viewState.showError(error) }
            )
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
    }
}