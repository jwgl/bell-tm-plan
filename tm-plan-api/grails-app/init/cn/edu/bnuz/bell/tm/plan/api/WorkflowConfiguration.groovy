package cn.edu.bnuz.bell.tm.plan.api

import cn.edu.bnuz.bell.planning.SchemeReviewerService
import cn.edu.bnuz.bell.planning.VisionReviewerService
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.config.DefaultStateMachineConfiguration
import cn.edu.bnuz.bell.workflow.config.DefaultStateMachinePersistConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.persist.StateMachinePersister

@Configuration
@Import([DefaultStateMachineConfiguration, DefaultStateMachinePersistConfiguration])
class WorkflowConfiguration {
    @Bean('schemeStateMachineHandler')
    DomainStateMachineHandler schemeStateMachineHandler(
            StateMachine<State, Event> stateMachine,
            StateMachinePersister<State, Event, StateObject> persister,
            SchemeReviewerService reviewerService) {
        new DomainStateMachineHandler(stateMachine, persister, reviewerService)
    }

    @Bean('visionStateMachineHandler')
    DomainStateMachineHandler visionStateMachineHandler(
            StateMachine<State, Event> stateMachine,
            StateMachinePersister<State, Event, StateObject> persister,
            VisionReviewerService reviewerService) {
        new DomainStateMachineHandler(stateMachine, persister, reviewerService)
    }
}
