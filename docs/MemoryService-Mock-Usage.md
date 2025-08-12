# MemoryService Mock Implementation

## Overview

The `MemoryServiceImpl` is a simple mock implementation of the `MemoryService` interface designed for API development and testing. Instead of making actual external calls to a Memory storage system, it returns predefined mock data that can be easily customized through configuration.

## Features

- **Configurable Mock Data**: All mock data can be customized through application properties
- **Realistic Data Structure**: Returns properly structured `Inspirations` and `PersonalPreferences` objects
- **Console Logging**: Logs travel demand updates for debugging purposes
- **Always Healthy**: Health check always returns `true` for development convenience

## Configuration

### Default Mock Data

The service comes with sensible default mock data defined in `application.yml`:

```yaml
mock:
  memory:
    user-data:
      recent-focus-destinations:
        - "Japan"
        - "Europe" 
        - "Southeast Asia"
        - "New Zealand"
      travel-styles:
        - "Cultural"
        - "Adventure"
        - "Relaxation"
        - "Food & Drink"
        - "Photography"
      likes:
        - "Museums and galleries"
        - "Local cuisine"
        - "Nature and hiking"
        - "Photography"
        - "Historical sites"
        - "Local markets"
        - "Beach activities"
        - "Mountain views"
      hates:
        - "Crowded tourist traps"
        - "Extreme sports"
        - "Long flights over 12 hours"
        - "Very expensive restaurants"
        - "Rainy weather destinations"
      age: 28
```

### Customizing Mock Data

You can override any of these values in your environment-specific configuration files:

#### For Development (`application-dev.yml`):
```yaml
mock:
  memory:
    user-data:
      age: 25
      recent-focus-destinations:
        - "Australia"
        - "South America"
      likes:
        - "Adventure sports"
        - "Wildlife photography"
```

#### For Testing:
```java
@TestPropertySource(properties = {
    "mock.memory.user-data.age=30",
    "mock.memory.user-data.recent-focus-destinations[0]=TestDestination1",
    "mock.memory.user-data.recent-focus-destinations[1]=TestDestination2"
})
```

## API Behavior

### `getInspirations(String userId)`
Returns an `Inspirations` object containing:
- **Recent Focus**: Destinations from configuration with priority ordering (1, 2, 3, ...)
- **Last 5 Year Visits**: Hardcoded realistic visit history with dates and locations
- **Travel Style**: List from configuration
- **Age**: Value from configuration

### `getPersonalPreferences(String userId)`
Returns a `PersonalPreferences` object containing:
- **Likes**: List from configuration
- **Hates**: List from configuration

### `updateUserHistory(String userId, TravelDemand travelDemand)`
- Logs the received travel demand to console for debugging
- Does not persist data (empty implementation)
- Always succeeds without throwing exceptions

### `isServiceHealthy()`
- Always returns `true`
- Useful for development where you don't want service health checks to fail

## Usage Examples

### Basic Usage
```java
@Autowired
private MemoryService memoryService;

public void example() {
    // Get user inspirations
    Inspirations inspirations = memoryService.getInspirations("user123");
    System.out.println("User age: " + inspirations.getAge());
    System.out.println("Recent focus: " + inspirations.getRecentFocus());
    
    // Get personal preferences
    PersonalPreferences prefs = memoryService.getPersonalPreferences("user123");
    System.out.println("Likes: " + prefs.getLikes());
    System.out.println("Hates: " + prefs.getHates());
    
    // Update user history (will log to console)
    TravelDemand demand = new TravelDemand();
    demand.setMustGoDestinations(Arrays.asList("Tokyo", "Kyoto"));
    demand.setDays(7);
    demand.setPassenger(2);
    memoryService.updateUserHistory("user123", demand);
}
```

### Testing with Custom Data
```java
@SpringBootTest
@TestPropertySource(properties = {
    "mock.memory.user-data.age=35",
    "mock.memory.user-data.likes[0]=Custom Like 1",
    "mock.memory.user-data.likes[1]=Custom Like 2"
})
class MyServiceTest {
    @Autowired
    private MemoryService memoryService;
    
    @Test
    void testWithCustomMockData() {
        Inspirations inspirations = memoryService.getInspirations("test-user");
        assertEquals(35, inspirations.getAge());
        
        PersonalPreferences prefs = memoryService.getPersonalPreferences("test-user");
        assertTrue(prefs.getLikes().contains("Custom Like 1"));
    }
}
```

## Benefits for API Development

1. **No External Dependencies**: Develop and test APIs without needing actual Memory service
2. **Predictable Data**: Always returns consistent, realistic data for testing
3. **Easy Customization**: Modify mock data through configuration without code changes
4. **Fast Development**: No network calls or external service setup required
5. **Debugging Support**: Console logging helps track API interactions

## Switching to Real Implementation

When ready to use the actual Memory service, simply:

1. Create a new implementation class (e.g., `MemoryServiceHttpImpl`)
2. Add `@Primary` annotation or use profiles to switch implementations
3. Configure the actual service endpoints in `external.memory.*` properties

The interface remains the same, so no changes are needed in consuming code.