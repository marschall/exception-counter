/*-
* Copyright (c) 2011 Ed Schouten <ed@FreeBSD.org>
* David Chisnall <theraven@FreeBSD.org>
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
* 1. Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
* OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
* HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
* LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
*
* $FreeBSD: src/include/stdatomic.h,v 1.10.2.2 2012/05/30 19:21:54 theraven Exp $
*/

#if __STDC_VERSION__ >= 201112L
// https://github.com/jeffhammond/HPCInfo/blob/master/c11/atomics.c
#include <stdatomic.h>
#else
// https://gist.github.com/nhatminhle/5181506
#include <stddef.h>
#include <stdint.h>

/*
 * Detect compiler features.
 */

#if !defined(__has_extension)
#define __has_extension(x) 0
#endif
#if !defined(__GNUC_PREREQ__)
#if defined(__GNUC__) && defined(__GNUC_MINOR__)
#define __GNUC_PREREQ__(maj, min) ((__GNUC__ << 16) + __GNUC_MINOR__ >= ((maj) << 16) + (min))
#else
#define __GNUC_PREREQ__(maj, min) 0
#endif
#endif
 
#if !defined(__CLANG_ATOMICS) && !defined(__GNUC_ATOMICS)
// __has_feature(c_atomic) does not work for whatever reason
#if __has_extension(c_atomic)
#define __CLANG_ATOMICS
#elif __GNUC_PREREQ__(4, 7)
#define __GNUC_ATOMICS
#elif !defined(__GNUC__)
#error "stdatomic.h does not support your compiler"
#endif
#endif
 
#if !defined(__CLANG_ATOMICS)
#define _Atomic(T) struct { volatile __typeof__(T) __val; }
#endif

enum memory_order {
    memory_order_relaxed = __ATOMIC_RELAXED,
    memory_order_consume = __ATOMIC_CONSUME,
    memory_order_acquire = __ATOMIC_ACQUIRE,
    memory_order_release = __ATOMIC_RELEASE,
    memory_order_acq_rel = __ATOMIC_ACQ_REL,
    memory_order_seq_cst = __ATOMIC_SEQ_CST
};
 
typedef enum memory_order memory_order;

#if defined(__CLANG_ATOMICS)
/*
 * Clang implementations.
 */
#define ATOMIC_VAR_INIT(value)  (value)
#define atomic_load_explicit(object, order)                            \
        __c11_atomic_load(object, order)
#define atomic_exchange_explicit(object, desired, order)               \
        __c11_atomic_exchange(object, desired, order)
#define atomic_fetch_add_explicit(object, operand, order)              \
        __c11_atomic_fetch_add(object, operand, order)
#elif defined(__GNUC_ATOMICS)
/*
 * GCC implementations.
 */
#define ATOMIC_VAR_INIT(value)  { .__val = (value) }
#define atomic_load_explicit(object, order)                            \
        __atomic_load_n(&(object)->__val, order)
#define atomic_exchange_explicit(object, desired, order)               \
        __atomic_exchange_n(&(object)->__val, desired, order)
#define atomic_fetch_add_explicit(object, operand, order)              \
        __atomic_fetch_add(&(object)->__val, operand, order)
#else
/*
 * Unsupported compiler and version combination.
 */
#error atomics not supported and can not be emulated
#endif

/*
 * Convenience functions.
 */

#define atomic_load(object)                                            \
        atomic_load_explicit(object, memory_order_seq_cst)

#define atomic_exchange(object, desired)                               \
        atomic_exchange_explicit(object, desired, memory_order_seq_cst)

#define atomic_fetch_add(object, operand)                              \
        atomic_fetch_add_explicit(object, operand, memory_order_seq_cst)

#endif
