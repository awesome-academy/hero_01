package com.sun.hero_01.data.source

import com.sun.hero_01.data.source.remote.OnFetchDataJsonListener

interface HeroDataSource {

    interface Local{
    }

    interface Remote {
        fun <T> getDataHeroDetail(
            id: String,
            listener: OnFetchDataJsonListener<T>
        )
    }
}
