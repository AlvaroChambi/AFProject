package es.developers.achambi.afines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import es.developer.achambi.coreframework.ui.BaseFragment
import es.developer.achambi.coreframework.ui.Screen
import es.developers.achambi.afines.home.OverviewPresenter
import es.developers.achambi.afines.home.ui.TaxPresentation
import kotlinx.android.synthetic.main.notification_item_layout.*

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
        get() = R.layout.notification_item_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AfinesApplication.overviewPresenterFactory.build(this, lifecycle)
        adapter = Adapter()
        taxes_recycler_view.layoutManager = LinearLayoutManager(activity)
        taxes_recycler_view.adapter = adapter
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
        presenter.onViewSetup()
        card_view_action_button.setOnClickListener { presenter.navigateToProfile() }
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
    private lateinit var data: SortedData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(parent)
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

class SortedData(klass: Class<TaxPresentation>, callback: Callback<TaxPresentation>) :
    SortedList<TaxPresentation>(klass, callback)

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