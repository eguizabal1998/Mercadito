package com.basicdeb.mercadito.ui.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.basicdeb.mercadito.ui.estados.CompletadoFragment
import com.basicdeb.mercadito.ui.estados.PendienteFragment
import com.basicdeb.mercadito.ui.estados.ProgresoFragment

class ViewPagerAdapter (fa: Fragment): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            1 -> { PendienteFragment()}
            2 -> { ProgresoFragment()}
            3 -> { CompletadoFragment()}
            else -> PendienteFragment()
        }

    }
}