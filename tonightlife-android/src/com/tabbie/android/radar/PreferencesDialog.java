package com.tabbie.android.radar;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PreferencesDialog extends Dialog {
	
	private final ArrayAdapter<CharSequence> ageAdapter;
	private final ArrayAdapter<CharSequence> costAdapter;
	private final ArrayAdapter<CharSequence> energyAdapter;

	public PreferencesDialog(final Context context, final int contentView) {
		super(context);
		
		this.setContentView(contentView);
		
		ageAdapter = ArrayAdapter.createFromResource(context,
    			R.array.age_array, android.R.layout.simple_spinner_item);
		costAdapter = ArrayAdapter.createFromResource(context,
				R.array.cost_array, android.R.layout.simple_spinner_item);
		energyAdapter = ArrayAdapter.createFromResource(context,
				R.array.energy_array, android.R.layout.simple_spinner_item);
		
		ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		costAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		energyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		final Spinner ageSpinner = (Spinner) this.findViewById(R.id.preferences_age_spinner);
		final Spinner costSpinner = (Spinner) this.findViewById(R.id.preferences_cost_spinner);
		final Spinner energySpinner = (Spinner) this.findViewById(R.id.preferences_energy_spinner);
		
		ageSpinner.setPrompt("Select an age range");
		costSpinner.setPrompt("Select price options");
		energySpinner.setPrompt("Select event vibe");
		
		ageSpinner.setAdapter(ageAdapter);
		costSpinner.setAdapter(costAdapter);
		energySpinner.setAdapter(energyAdapter);
	}

}
