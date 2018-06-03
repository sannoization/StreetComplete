package de.westnordost.streetcomplete.quests.max_height;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.westnordost.streetcomplete.R;
import de.westnordost.streetcomplete.quests.AbstractQuestFormAnswerFragment;
import de.westnordost.streetcomplete.view.dialogs.AlertDialogBuilder;

public class AddMaxHeightForm extends AbstractQuestFormAnswerFragment
{
	public static final String
		MAX_HEIGHT = "max_height",
		NO_SIGN = "no_sign";

	public static final int
		IS_BELOW_DEFAULT = 1,
		IS_DEFAULT = 2,
		IS_NOT_INDICATED = 3;

	private EditText heightInput, feetInput, inchInput;

	private boolean isMetric;

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
									   Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		isMetric = getCountryInfo().getHeightUnits().get(0).equals("m");

		setMaxHeightSignLayout(R.layout.quest_max_height);
		addOtherAnswers();

		return view;
	}

	private void setMaxHeightSignLayout(int resourceId)
	{
		View contentView = setContentView(getCurrentCountryResources().getLayout(resourceId));

		heightInput = contentView.findViewById(R.id.maxHeightInput);
		feetInput = contentView.findViewById(R.id.maxHeightFeetInput);
		inchInput = contentView.findViewById(R.id.maxHeightInchInput);
	}

	private void addOtherAnswers()
	{
		//TODO: use a spinner for this
		/*if (getCountryInfo().getHeightUnits().size() == 2)
		{
			if (isMetric)
			{
				addOtherAnswer(R.string.quest_maxheight_answer_imperial_unit, () ->
				{
					isMetric = false;
					setMaxHeightSignLayout(getMaxHeightLayoutResourceId());
				});
			}
			else
			{
				addOtherAnswer(R.string.quest_maxheight_answer_metric_unit, () ->
				{
					isMetric = true;
					setMaxHeightSignLayout(getMaxHeightLayoutResourceId());
				});
			}
		}*/

		addOtherAnswer(R.string.quest_maxheight_answer_noSign, () ->
		{
			final String
				restrictsTraffic = getResources().getString(R.string.quest_maxheight_answer_restrictsTraffic),
				noTrafficRestriction = getResources().getString(R.string.quest_maxheight_answer_noTrafficRestriction),
				cantEstimate = getResources().getString(R.string.quest_maxheight_answer_cantEstimate);

			final List<String> answers = new ArrayList<>(3);
			answers.add(restrictsTraffic);
			answers.add(noTrafficRestriction);
			answers.add(cantEstimate);

			DialogInterface.OnClickListener onSelect = new DialogInterface.OnClickListener()
			{
				Integer selection = null;

				@Override public void onClick(DialogInterface dialog, int which)
				{
					if (which >= 0)
					{
						selection = which;
						((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
					}
					else if (which == DialogInterface.BUTTON_POSITIVE)
					{
						if(selection == null || selection < 0 || selection >= answers.size()) return;
						onAnswer();
					}
				}

				private void onAnswer()
				{
					String answer = answers.get(selection);
					Bundle data = new Bundle();
					int type = 0;
					if(answer.equals(restrictsTraffic))	type = IS_BELOW_DEFAULT;
					if(answer.equals(noTrafficRestriction))	type = IS_DEFAULT;
					if(answer.equals(cantEstimate))    type = IS_NOT_INDICATED;
					data.putInt(NO_SIGN, type);
					applyImmediateAnswer(data);
				}
			};

			AlertDialog dlg = new AlertDialogBuilder(getActivity())
				.setSingleChoiceItems(answers.toArray(new String[0]), -1, onSelect)
				.setTitle(R.string.quest_maxheight_answer_noSign_question)
				.setPositiveButton(android.R.string.ok, onSelect)
				.setNegativeButton(android.R.string.cancel, null)
				.show();

			dlg.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
		});
	}

	@Override protected void onClickOk()
	{
		if(!hasChanges())
		{
			Toast.makeText(getActivity(), R.string.no_changes, Toast.LENGTH_SHORT).show();
			return;
		}

		if(userSelectedUnrealisticHeight())
		{
			confirmUnusualInput(this::applyMaxHeightFormAnswer);
		}
		else
		{
			applyMaxHeightFormAnswer();
		}
	}

	private boolean userSelectedUnrealisticHeight()
	{
		int height = getHeightFromInput().getIntegerPart();
		double heightInMeter = isMetric ? height : feetToMeter(height);
		return heightInMeter > 6 || heightInMeter < 2;
	}

	private static double feetToMeter(double feet)
	{
		return feet / 3.2808;
	}

	private void applyMaxHeightFormAnswer()
	{
		String height = getHeightFromInput().toString();

		Bundle answer = new Bundle();
		answer.putString(MAX_HEIGHT, height);
		applyFormAnswer(answer);
	}

	private Height getHeightFromInput()
	{
		if (isMetric)
		{
			if (!heightInput.getText().toString().isEmpty())
			{
				String[] parts = heightInput.getText().toString().split(".");
				return new Height(parts[0], parts[1], Height.METRIC);
			}
			return new Height();
		}
		else
		{
			if (!feetInput.getText().toString().isEmpty() && !inchInput.getText().toString().isEmpty())
			{
				return new Height(feetInput.getText().toString(), inchInput.getText().toString(), Height.IMPERIAL);
			}
			return new Height();
		}
	}

	private void confirmUnusualInput(final Runnable callback)
	{
		new AlertDialogBuilder(getActivity())
			.setTitle(R.string.quest_generic_confirmation_title)
			.setMessage(R.string.quest_maxheight_unusualInput_confirmation_description)
			.setPositiveButton(R.string.quest_generic_confirmation_yes, (dialog, which) -> callback.run())
			.setNegativeButton(R.string.quest_generic_confirmation_no, null)
			.show();
	}

	@Override public boolean hasChanges()
	{
		return !getHeightFromInput().isEmpty();
	}
}
