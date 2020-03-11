package es.developers.achambi.afines.notifications

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import es.developer.achambi.coreframework.ui.BaseSearchListFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developer.achambi.coreframework.ui.SearchAdapterDecorator
import es.developers.achambi.afines.AfinesApplication
import es.developers.achambi.afines.R
import es.developers.achambi.afines.home.NotificationPresentation
import kotlinx.android.synthetic.main.notification_item_layout.view.*

interface NotificationsScreen: Screen {
    fun showNotifications(notifications: ArrayList<NotificationPresentation>)
    fun showProgress()
    fun showProgressFinished()
}

class NotificationsFragment: BaseSearchListFragment(), AdapterListener, NotificationsScreen {
    private lateinit var adapter: Adapter
    private lateinit var presenter: NotificationsPresenter
    companion object {
        fun newInstance(): NotificationsFragment {
            return NotificationsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.notificationsPresenterFactory.build(this, lifecycle)
    }

    override fun onDataSetup() {
        super.onDataSetup()
        presenter.onDataSetup()
    }

    override fun provideAdapter(): SearchAdapterDecorator<*, *> {
        adapter = Adapter(this)
        return adapter
    }

    override fun onNotificationGoToSelected(item: NotificationPresentation) {

    }

    override fun showNotifications(notifications: ArrayList<NotificationPresentation>) {
        adapter.data = notifications
        presentAdapterData()
    }

    override fun showProgress() {
    }

    override fun showProgressFinished() {
    }
}

interface AdapterListener {
    fun onNotificationGoToSelected(item: NotificationPresentation)
}

class Holder(root: View): RecyclerView.ViewHolder(root)

class Adapter(val listener: AdapterListener): SearchAdapterDecorator<NotificationPresentation, Holder>() {
    override fun getLayoutResource(): Int {
        return R.layout.notification_item_layout
    }

    override fun createViewHolder(rootView: View, rootData: SortedList<*>): Holder {
        val holder = Holder(rootView)
        holder.itemView.notification_item_go_to.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onNotificationGoToSelected(rootData.get(position) as NotificationPresentation)
            }
        }
        return holder
    }

    override fun bindViewHolder(holder: Holder, item: NotificationPresentation) {
        holder.itemView.notification_item_textview.text = item.message
    }

}

