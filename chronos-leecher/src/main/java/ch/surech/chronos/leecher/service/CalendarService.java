package ch.surech.chronos.leecher.service;

import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.EventCollectionRequest;
import com.microsoft.graph.requests.GraphServiceClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarService.class);

    @Autowired
    private GraphService graphService;

    public List<Event> getEventsFromCalendar(@NonNull User user){
        return getEventsFromCalendar(user.id);
    }

    public List<Event> getEventsFromCalendar(String userId){
        GraphServiceClient graphClient = graphService.getGraphClient();

        LocalDateTime start = LocalDateTime.now().minusWeeks(2);
        LocalDateTime end = LocalDateTime.now().plusWeeks(2);

        String startString = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(start);
        String endString = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(end);

        List<Event> result = new ArrayList<>();

        final List<Option> options = new LinkedList<Option>();
        options.add(new QueryOption("startDateTime", startString));
        options.add(new QueryOption("endDateTime", endString));
        options.add(new QueryOption("top", "500"));

        EventCollectionRequest request = graphClient
            .users(userId)
            .calendar()
            .calendarView()
            .buildRequest(options);

        loadAllEvents(request, result);
        return result;
    }

    private void loadAllEvents(EventCollectionRequest buildRequest, List<Event> result) {
        EventCollectionPage events = buildRequest.get();
        LOGGER.info("Loaded {} Events...", events.getCurrentPage().size());

        List<Event> currentPage = events.getCurrentPage();
        result.addAll(currentPage);

        if(events.getNextPage() != null){
            loadAllEvents(events.getNextPage().buildRequest(), result);
        }
    }
}
