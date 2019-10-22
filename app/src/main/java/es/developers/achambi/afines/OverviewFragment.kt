package es.developers.achambi.afines

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.developer.achambi.coreframework.ui.BaseSearchListFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developer.achambi.coreframework.ui.SearchAdapterDecorator
import es.developers.achambi.afines.home.NotificationPresentation
import es.developers.achambi.afines.home.NotificationsPresenter

class OverviewFragment : BaseSearchListFragment(), NotificationsScreen {
    override fun showNotifications(notifications: ArrayList<NotificationPresentation>) {
        adapter.data = notifications
        presentAdapterData()
    }

    private lateinit var adapter: NotificationsAdapter
    private lateinit var presenter: NotificationsPresenter

    override fun provideAdapter(): SearchAdapterDecorator<NotificationPresentation, ViewHolder> {
        adapter = NotificationsAdapter()
        return adapter
    }

    companion object{
        fun newInstance() : OverviewFragment{
            return OverviewFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.notificationsPresenterFactory.build(this, lifecycle)
    }

    override fun onViewSetup(view: View) {
        super.onViewSetup(view)
        activity?.setTitle(R.string.overview_menu_text)
    }

    override fun onDataSetup() {
        super.onDataSetup()
        presenter.onDataSetup()
    }
}

class NotificationsAdapter: SearchAdapterDecorator<NotificationPresentation, ViewHolder>() {
    override fun getLayoutResource(): Int {
        return R.layout.notification_item_layout
    }

    override fun createViewHolder(rootView: View): ViewHolder {
        return ViewHolder(rootView)
    }

    override fun bindViewHolder(holder: ViewHolder, item: NotificationPresentation) {
        holder.itemView.findViewById<TextView>(R.id.notification_item_message).text = item.message
    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
interface NotificationsScreen: Screen {
    fun showNotifications(notifications: ArrayList<NotificationPresentation>)
}