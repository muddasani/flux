/*
 * Copyright 2012-2016, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.flux.representation;

import com.flipkart.flux.api.EventDefinition;
import com.flipkart.flux.api.StateDefinition;
import com.flipkart.flux.api.StateMachineDefinition;
import com.flipkart.flux.dao.iface.StateMachinesDAO;
import com.flipkart.flux.domain.Event;
import com.flipkart.flux.domain.State;
import com.flipkart.flux.domain.StateMachine;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>StateMachinePersistenceService</code> class converts user provided state machine entity definition to domain type object and stores in DB.
 * @author shyam.akirala
 */
@Singleton
public class StateMachinePersistenceService {

    private StateMachinesDAO stateMachinesDAO;

    private EventPersistenceService eventPersistenceService;

    @Inject
    public StateMachinePersistenceService(StateMachinesDAO stateMachinesDAO, EventPersistenceService eventPersistenceService) {
        this.stateMachinesDAO = stateMachinesDAO;
        this.eventPersistenceService = eventPersistenceService;
    }

    /**
     * Converts state machine definition to state machine domain object and saves in DB.
     * @param stateMachineDefinition
     * @return saved state machine object
     */
    public StateMachine createStateMachine(StateMachineDefinition stateMachineDefinition) {
        Set<StateDefinition> stateDefinitions = stateMachineDefinition.getStates();
        Set<State> states = new HashSet<>();
        Set<Event> eventSet = new HashSet<>();

        for(StateDefinition stateDefinition : stateDefinitions) {
            State state = convertStateDefinitionToState(stateDefinition, eventSet);
            states.add(state);
        }

        StateMachine stateMachine = new StateMachine(stateMachineDefinition.getVersion(),
                stateMachineDefinition.getName(),
                stateMachineDefinition.getDescription(),
                states);

        stateMachinesDAO.create(stateMachine);

        for(Event event: eventSet) {
            event.setStateMachineInstanceId(stateMachine.getId());
            eventPersistenceService.persistEvent(event);
        }

        return stateMachine;
    }

    /**
     * Converts state definition to state domain object.
     * @param stateDefinition
     * @return state
     */
    private State convertStateDefinitionToState(StateDefinition stateDefinition, Set<Event> eventSet) {

        try {
            Set<EventDefinition> eventDefinitions = stateDefinition.getDependencies();
            HashSet<String> events = null;
            if(eventDefinitions != null) {
                events = new HashSet<>();
                for(EventDefinition e : eventDefinitions) {
                    events.add(e.getName());
                    eventSet.add(eventPersistenceService.convertEventDefinitionToEvent(e));
                }
            }
            State state = new State(stateDefinition.getVersion(),
                    stateDefinition.getName(),
                    stateDefinition.getDescription(),
                    stateDefinition.getOnEntryHook(),
                    stateDefinition.getTask(),
                    stateDefinition.getOnExitHook(),
                    events,
                    stateDefinition.getRetryCount(),
                    stateDefinition.getTimeout());
            return state;
        } catch (Exception e) {
            throw new IllegalRepresentationException("Unable to create state domain object", e);
        }
    }

}