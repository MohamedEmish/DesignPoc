package com.example.designpoc.utils.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.designpoc.utils.permission.PermissionResult.DENIED
import com.example.designpoc.utils.permission.PermissionResult.GRANTED
import com.example.designpoc.utils.permission.PermissionResult.RATIONAL_REASON
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified R : ActivityResultLauncher<Array<String>>> Fragment.registerMultiplePermissionLauncher(
    noinline onPermissionResult: (permissions: Array<String>, permissionResult: PermissionResult) -> Unit,
): ReadOnlyProperty<Fragment, R> = MultiplePermissionResultDelegate(this, onPermissionResult)

class MultiplePermissionResultDelegate<R : ActivityResultLauncher<Array<String>>>(
    private val fragment: Fragment,
    private val onPermissionResult: (permissions: Array<String>, permissionResult: PermissionResult) -> Unit,
) : ReadOnlyProperty<Fragment, R> {

    private var permissionResult: ActivityResultLauncher<Array<String>>? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.apply {
                    permissionResult = registerForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) { resultsMap ->
                        val deniedList = resultsMap.filter { !it.value }.keys
                        val rationalList = deniedList.filter { shouldShowRequestPermissionRationale(it) }

                        when {
                            deniedList.isEmpty() -> onPermissionResult(resultsMap.keys.toTypedArray(), GRANTED)
                            rationalList.isNotEmpty() -> onPermissionResult(
                                rationalList.toTypedArray(),
                                RATIONAL_REASON
                            )
                            else -> onPermissionResult(deniedList.toTypedArray(), DENIED)
                        }
                    }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                permissionResult = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): R {
        permissionResult?.let { return (it as R) }
        error("Failed to Initialize Permission")
    }
}