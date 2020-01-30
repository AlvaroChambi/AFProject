package es.developers.achambi.afines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.home.OverviewPresenter
import es.developers.achambi.afines.home.ui.TaxPresentation
import kotlinx.android.synthetic.main.overview_fragment_layout.*

class OverviewFragment : BaseFragment(), NotificationsScreen {
    private lateinit var presenter: OverviewPresenter
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var adapter: Adapter

    companion object{
        fun newInstance() : OverviewFragment{
            return OverviewFragment()
        }
    }

    override val layoutResource: Int
        get() = R.layout.overview_fragment_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.overviewPresenterFactory.build(this, lifecycle)
        adapter = Adapter()
    }

    override fun onStart() {
        super.onStart()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                showRejectInvoicesNotification()
            }
        }
        presenter.registerBroadcast(broadcastReceiver)
    }

    override fun onStop() {
        super.onStop()
        presenter.unregisterBroadcast(broadcastReceiver)
    }

    override fun onViewSetup(view: View) {
        activity?.setTitle(R.string.app_name)
        taxes_recycler_view.layoutManager = LinearLayoutManager(activity)
        taxes_recycler_view.adapter = adapter
        presenter.onViewSetup()
        password_notification_action_button.setOnClickListener { presenter.navigateToProfile() }
        rejected_invoice_action_button.setOnClickListener { presenter.navigateToInvoices() }
    }

    override fun showTaxDates(taxes: ArrayList<TaxPresentation>) {
        adapter.setData(taxes)
    }

    override fun showUpdatePasswordNotification() {
        update_password_frame.visibility = View.VISIBLE
    }

    override fun showRejectInvoicesNotification() {
        rejected_invoices_frame.visibility = View.VISIBLE
    }
}

class Adapter : RecyclerView.Adapter<Holder>() {
    private val data: SortedList<TaxPresentation>
    init {
        data = SortedList(TaxPresentation::class.java,
            object : SortedListAdapterCallback<TaxPresentation>(this) {
            override fun areItemsTheSame(
                item1: TaxPresentation,
                item2: TaxPresentation
            ): Boolean = item1.id == item2.id

            override fun compare(o1: TaxPresentation, o2: TaxPresentation): Int = o1.sortValue.compareTo(o2.sortValue)

            override fun areContentsTheSame(
                oldItem: TaxPresentation,
                newItem: TaxPresentation
            ): Boolean = oldItem.id == newItem.id
        })
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tax_date_item_layout, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size()
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind( data.get(position) )
    }

    fun setData(taxes: ArrayList<TaxPresentation>) {
        data.addAll(taxes)
    }
}

class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(taxDate: TaxPresentation) {
        itemView.findViewById<TextView>(R.id.taxes_name_text).text = taxDate.name
        itemView.findViewById<TextView>(R.id.taxes_trimester_text).text = taxDate.trimester
        itemView.findViewById<TextView>(R.id.taxes_days_left_text).text = taxDate.daysLeft
        itemView.findViewById<TextView>(R.id.taxes_date_text).text = taxDate.date
    }
}

interface NotificationsScreen : Screen {
    fun showUpdatePasswordNotification()
    fun showRejectInvoicesNotification()
    fun showTaxDates(taxes: ArrayList<TaxPresentation>)
}