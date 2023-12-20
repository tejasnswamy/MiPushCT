package com.example.xioamiintegration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.InAppNotificationButtonListener
import com.example.xioamiintegration.databinding.FragmentFirstBinding
import java.util.HashMap

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment()  {

    private var _binding: FragmentFirstBinding? = null
    private var _ctInstance: CleverTapAPI ? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _ctInstance = CleverTapAPI.getDefaultInstance(this.context)




        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonInAppDemo.setOnClickListener {
            _ctInstance?.pushEvent("In app Event")
        }

        binding.buttonNativeDisplay.setOnClickListener {
            _ctInstance?.pushEvent("NativeDisplay Event")
            requireActivity().run{
                startActivity(Intent(this, NativeDisplayActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}