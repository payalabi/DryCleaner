package controller;

import java.text.ParseException;
import java.util.Date;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import service.BusinessHourCalculator;

/**
 * @author payalabichandani
 *
 */
@RestController

public class HomeController {
	@RequestMapping(value = "/getdate/{timeInterval}/{startDateTime}", method = RequestMethod.GET)
	@ResponseBody
	public String calculateDeadlinel(@PathVariable long timeInterval, @PathVariable String startDateTime) throws ParseException {
		BusinessHourCalculator businessHourCalculator = new BusinessHourCalculator("09:00", "15:00");
		businessHourCalculator.setOpeningHours(service.BusinessHourCalculator.DayOfWeek.FRIDAY, "10:00", "17:00");
		businessHourCalculator.setOpeningHours("2010-12-24", "8:00", "13:00");
		businessHourCalculator.setClosed(service.BusinessHourCalculator.DayOfWeek.SUNDAY,
				service.BusinessHourCalculator.DayOfWeek.WEDNESDAY);
		businessHourCalculator.setClosed("2010-12-25");
		Date	date = businessHourCalculator.calculateDeadlinel(timeInterval, startDateTime);
		return date.toString();
	}

}
