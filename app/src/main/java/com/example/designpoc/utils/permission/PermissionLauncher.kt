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

inline fun <reified R : ActivityResultLauncher<String>> Fragment.registerPermissionLauncher(
    permission: String,
    noinline onPermissionResult: (permission: String, permissionResult: PermissionResult) -> Unit,
): ReadOnlyProperty<Fragment, R> = PermissionResultDelegate(this, permission, onPermissionResult)

class PermissionResultDelegate<R : ActivityResultLauncher<String>>(
    private val fragment: Fragment,
    private val permission: String,
    private val onPermissionResult: (permission: String, permissionResult: PermissionResult) -> Unit,
) : ReadOnlyProperty<Fragment, R> {

    private var permissionResult: ActivityResultLauncher<String>? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.apply {
                    permissionResult = registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->

                        when {
                            isGranted -> onPermissionResult(permission, GRANTED)
                            shouldShowRequestPermissionRationale(permission) -> onPermissionResult(
                                permission,
                                RATIONAL_REASON
                            )
                            else -> onPermissionResult(permission, DENIED)
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

enum class PermissionResult {
    GRANTED,
    DENIED,
    RATIONAL_REASON,
}
