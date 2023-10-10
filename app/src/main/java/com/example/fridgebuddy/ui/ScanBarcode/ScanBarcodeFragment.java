package com.example.fridgebuddy.ui.ScanBarcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fridgebuddy.databinding.FragmentScanbarcodeBinding;

public class ScanBarcodeFragment extends Fragment {

    private @NonNull FragmentScanbarcodeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanBarcodeViewModel viewModel = new ViewModelProvider(this).get(ScanBarcodeViewModel.class);
        ScanBarcodeViewModel scanBarcodeViewModel =
                new ViewModelProvider(this).get(ScanBarcodeViewModel.class);

        binding = FragmentScanbarcodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        scanBarcodeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}