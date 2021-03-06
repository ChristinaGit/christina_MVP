package christina.library.android.architecture.mvp.screen_view.task

import christina.library.android.architecture.mvp.screen_view.content.ContentScreenView

interface TaskScreenView<in Result, in Progress, in Error> : ContentScreenView<Result> {
    val progressScreenView: ContentScreenView<Progress>
    val errorScreenView: ContentScreenView<Error>
}