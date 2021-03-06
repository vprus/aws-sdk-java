/*
 * Copyright 2011-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling;

import com.amazonaws.services.dynamodbv2.datamodeling.StandardTypeConverters.Scalar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Annotation for marking a property as an optimistic locking version attribute.
 *
 * <pre class="brush: java">
 * &#064;DynamoDBVersioned
 * public Long getRecordVersionNumber()
 * </pre>
 *
 * <p>Alternately, the convinience annotation {@link DynamoDBVersionAttribute}
 * may be used if combining with an attribute name on a field/getter.</p>
 *
 * <p>Only nullable, integral numeric types (e.g. Integer, Long) can be used as
 * version properties. On a save() operation, the {@link DynamoDBMapper} will
 * attempt to increment the version property and assert that the service's value
 * matches the client's.</p>
 *
 * <p>New objects will be assigned a version of 1 when saved.</p>
 *
 * <p>Note that for batchWrite, and by extension batchSave and batchDelete,
 * <b>no version checks are performed</b>, as required by the
 * {@link com.amazonaws.services.dynamodbv2.AmazonDynamoDB#batchWriteItem(BatchWriteItemRequest)}
 * API.</p>
 *
 * <p>May be used as a meta-annotation.</p>
 *
 * @see com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute
 */
@DynamoDB
@DynamoDBAutoGenerated(generator=DynamoDBVersioned.Generator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface DynamoDBVersioned {

    /**
     * Version auto-generator.
     */
    static final class Generator<T> extends DynamoDBAutoGenerator.AbstractGenerator<T> {
        private final Sequence<T> sequence;

        public Generator(Class<T> targetType, DynamoDBVersioned annotation) {
            super(DynamoDBAutoGenerateStrategy.ALWAYS);
            this.sequence = Sequences.of(targetType);
        }

        @Override
        public final T generate(final T currentValue) {
            return currentValue == null ? sequence.init() : sequence.next(currentValue);
        }

        static interface Sequence<T> {
            public T init();
            public T next(final T o);
        }

        private static enum Sequences {
            BIG_INTEGER(Scalar.BIG_INTEGER, new Sequence<BigInteger>() {
                @Override
                public final BigInteger init() {
                    return BigInteger.ONE;
                }
                @Override
                public final BigInteger next(final BigInteger o) {
                    return o.add(BigInteger.ONE);
                }
            }),

            BYTE(Scalar.BYTE, new Sequence<Byte>() {
                @Override
                public final Byte init() {
                    return Byte.valueOf((byte)1);
                }
                @Override
                public final Byte next(final Byte o) {
                    return (byte)((o + 1) % Byte.MAX_VALUE);
                }
            }),

            INTEGER(Scalar.INTEGER, new Sequence<Integer>() {
                @Override
                public final Integer init() {
                    return Integer.valueOf(1);
                }
                @Override
                public final Integer next(final Integer o) {
                    return o + 1;
                }
            }),

            LONG(Scalar.LONG, new Sequence<Long>() {
                @Override
                public final Long init() {
                    return Long.valueOf(1L);
                }
                @Override
                public final Long next(final Long o) {
                    return o + 1L;
                }
            }),

            SHORT(Scalar.SHORT, new Sequence<Short>() {
                @Override
                public final Short init() {
                    return Short.valueOf((short)1);
                }
                @Override
                public final Short next(final Short o) {
                    return (short)(o + 1);
                }
            });

            private final Sequence<?> sequence;
            private final Scalar scalar;

            private Sequences(final Scalar scalar, final Sequence<?> sequence) {
                this.sequence = sequence;
                this.scalar = scalar;
            }

            private static final <T> Sequence<T> of(final Class<T> targetType) {
                for (final Sequences s : Sequences.values()) {
                    if (s.scalar.is(targetType)) {
                        return (Sequence<T>)s.sequence;
                    }
                }
                throw new DynamoDBMappingException(
                    "type [" + targetType + "] is not supported; allowed only " + Arrays.toString(Sequences.values())
                );
            }
        }
    }

}
