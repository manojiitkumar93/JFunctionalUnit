package com.jfunc.core;

import com.jfunc.validator.RequirementsWrapper;
import com.jfunc.validator.ValidatorUtil;

public class JFuncWorkerThread implements Runnable {

    private NonFunctionalityReason nonFunctionalityReason;
    private RequirementsWrapper requirementsWrapper;

    public JFuncWorkerThread(RequirementsWrapper requirementsWrapper,
            NonFunctionalityReason nonFunctionalityReasonInstance) {
        this.nonFunctionalityReason = nonFunctionalityReasonInstance;
        this.requirementsWrapper = requirementsWrapper;
    }

    @Override
    public void run() {
        ValidatorUtil.validate(requirementsWrapper.getClassMetaData(), nonFunctionalityReason,
                requirementsWrapper.skipLogStatements(), requirementsWrapper.skipPrintStatements());
    }

}
